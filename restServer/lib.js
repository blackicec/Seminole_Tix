var models = require('./models'),
	User = models.User,
	LoginToken = models.LoginToken;

function authenticateFromLoginToken (req, res, next) {
	var cookie = JSON.parse(req.cookies.logintoken);

	LoginToken.findOne({ userId: cookie.email,
						series: cookie.series,
						token: cookie.token }, (function(err, token) {
		if (!token) {
			res.json({success:false, error:'Not logged in.'});
			return;
		}

		User.findOne({ userId: token.userId }, function(err, user) {
			if (user) {
					req.session.user_id = user.id;
					req.currentUser = user;

					token.token = token.randomToken();
					token.save(function() {
						res.cookie('logintoken', token.cookieValue, { expires: new Date(Date.now() + 2 * 604800000), path: '/' });
						next();
					});
			} else {
				res.json({success:false, error:'Not logged in.'});
			}
		});
	}));
};

 exports.loadUser =  function (req, res, next) {
	if (req.session.userId) {
			User.findOne({'userId': req.session.userId}, function(err, user) {
		  		if (user) {
			    	req.currentUser = user;
			    	next();
			  	} else {
			    	res.json({success:false, error:'Not logged in.'});
			  	}
			});
	} else if (req.cookies.logintoken) {
		authenticateFromLoginToken(req, res, next);
	} else {
		res.json({success:false, error:'Not logged in.'});
	}
};