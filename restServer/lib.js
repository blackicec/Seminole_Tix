var models = require('./models');	// Mongoose models required for database manipulation

// Mongoose Models
var	User = models.User,					// Users Collection in MongoDB
	LoginToken = models.LoginToken;		// LoginTokens Collection in MongoDB

// Authenticate user with cookie
function authenticateFromLoginToken (req, res, next) {

	// Get cookie
	var cookie = JSON.parse(req.cookies.logintoken);

	// Search for token by cookie info
	LoginToken.findOne({ userId: cookie.email, series: cookie.series, token: cookie.token }, function(err, token) {
		
		if (!token) {
			res.json(
				{
					success:false, 
					error:'Not logged in.'
				});
			return;
		}

		// Find user by userId in token
		User.findOne({ userId: token.userId }, function(err, user) {
			if (user) {

				// Reinstate session
				req.session.user_id = user.id;
				req.currentUser = user;

				// Move on token to a new token for security purposes
				token.token = token.randomToken();
				token.save(function() {
					res.cookie('logintoken', token.cookieValue, { expires: new Date(Date.now() + 2 * 604800000), path: '/' });
					next();
				});

			} else
				res.json(
					{
						success:false, 
						error:'Not logged in.'
					});
		});
	});
};

// Load user from the database
exports.loadUser =  function (req, res, next) {

	// If session exists
	if (req.session.userId) {

			// Find user by userId from session
			User.findOne({'userId': req.session.userId}, function(err, user) {
		  		if (user) {

		  			// Load user and move on to callee
			    	req.currentUser = user;
			    	next();

			  	} else
			    	res.json(
			    		{
			    			success:false, 
			    			error:'Not logged in.'
			    		});
			});

	// Or if we have a cookie
	} else if (req.cookies.logintoken)
		authenticateFromLoginToken(req, res, next);

	else
		res.json(
			{
				success:false,
			 	error:'Not logged in.'
			});
};