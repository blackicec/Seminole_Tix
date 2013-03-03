var express = require('express'),
	mongoose = require('mongoose'),
	Session = require('connect-mongodb'),
	https = require('https'),
	http = require('http'),
	fs = require('fs'),
	models = require('./models'),
	routes = require('./routes'),
	lib = require('./lib');

var	User = models.User,
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

	app.post('/login', routes.login);

	app.get('/logout', routes.logout);

	app.post('/users', routes.users_register);

	app.get('/users', routes.users_view);

	// Games
	app.get('/games', routes.games_view);

	app.get('/game/:id', lib.loadUser, routes.game_view);

	app.post('/game/:id', lib.loadUser, routes.game_reserve);

	http.createServer(app).listen(80);
	https.createServer(options, app).listen(443);
	console.log('Seminole Tix REST Server listening on port 80 and 443');
});
