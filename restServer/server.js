var express = require('express'),			// Express middleware
	mongoose = require('mongoose'),			// Mongoose MongoDB connector
	Session = require('connect-mongodb'),	// Database connection to mongodb
	https = require('https'),				// https library
	http = require('http'),					// http library
	fs = require('fs'),						// filesystem library
	models = require('./models'),			// Mongoose models required for database manipulation
	routes = require('./routes'),			// route definitions
	lib = require('./lib');					// lib helper functions

// Mongoose Models
var	User = models.User,					// Users Collection in MongoDB
	Game = models.Game,					// Games Collection in MongoDB
	LoginToken = models.LoginToken;		// LoginTokens Collection in MongoDB

// Connect to mongodb
mongoose.connect('localhost', 'dunk', function(err){
	if(err) throw err;
});

// SSL Certificate
var options = {
  key: fs.readFileSync('/etc/ssl/self-signed/server.key'),
  cert: fs.readFileSync('/etc/ssl/self-signed/server.crt')
};

// Reference database
var db = mongoose.connection;

// Handle connection fail
db.on('error', console.error.bind(console, 'connection error:'));

// Setup app once we can access database
db.once('open', function callback() {

	// Enable support for POST parsing and cookie parsing
	var app = express();
	app.use(express.cookieParser());
	app.use(express.bodyParser());

	// Setup sessions
	var session = express.session(
		{
			store: new Session(
				{
					db: mongoose.connection.db, 
					maxAge: 300000
				}),
			secret: 's!p@r#o$S%^r&t' 
		})
  	app.use(session);

  	// Login
	app.post('/login', routes.login);

	// Logout
	app.get('/logout', routes.logout);

	// Register User
	app.post('/users', routes.users_register);

	// View Users
	app.get('/users', routes.users_view);

	// View User
	app.get('/user', lib.loadUser, routes.user_view)
	
	// View Games
	app.get('/games', routes.games_view);

	// View Game
	app.get('/game/:id', lib.loadUser, routes.game_view);

	// Reserve Ticket
	app.post('/game/:id', lib.loadUser, routes.game_reserve);

	// Start Server for HTTPS
	https.createServer(options, app).listen(443);
	console.log('Seminole Tix REST Server listening on port 443');
});
