var express = require('express'),
	mongoose = require('mongoose'),
	models = require('./models'),
	routes = require('./routes')
	Session = require('connect-mongodb'),
	check = require('validator').check,
	https = require('https'),
	http = require('http'),
	fs = require('fs'),
	User = models.User,
	Game = models.Game,
	LoginToken = models.LoginToken;

mongoose.connect('localhost', 'dunk', function(err){
	if(err) throw err;
});

var options = {
  key: fs.readFileSync('/etc/ssl/self-signed/server.key'),
  cert: fs.readFileSync('/etc/ssl/self-signed/server.crt')
};

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));

db.once('open', function callback() {

	var app = express();
	app.use(express.cookieParser());
	app.use(express.bodyParser());

	var session = express.session({store: new Session({db: mongoose.connection.db, maxAge: 300000}), secret: 's!p@r#o$S%^r&t' })
  	app.use(session);

	function authenticateFromLoginToken(req, res, next) {
		var cookie = JSON.parse(req.cookies.logintoken);

		LoginToken.findOne({ userId: cookie.email,
							series: cookie.series,
							token: cookie.token }, (function(err, token) {
			if (!token) {
				res.send(401,'Not logged in.');
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
					res.send(401, 'Not logged in.');
				}
			});
		}));
	}

  	function loadUser(req, res, next) {
		if (req.session.userId) {
				User.findOne({'userId': req.session.userId}, function(err, user) {
			  		if (user) {
				    	req.currentUser = user;
				    	next();
				  	} else {
				    	res.send(401,'Not logged in.');
				  	}
				});
		} else if (req.cookies.logintoken) {
			authenticateFromLoginToken(req, res, next);
		} else {
			res.send(401, 'Not logged in.');
		}
	}

	app.post('/login', routes.login);

	app.get('/logout', routes.logout);

	app.post('/users', routes.users_register);

	app.get('/users', routes.users_view);

	// Games
	app.get('/games', routes.games_view);

	app.get('/game/:id', loadUser, routes.game_view);

	app.post('/game/:id', loadUser, routes.game_reserve);

	http.createServer(app).listen(80);
	https.createServer(options, app).listen(443);
	console.log('Seminole Tix REST Server listening on port 80 and 443');
});
