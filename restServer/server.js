var express = require('express'),
	mongoose = require('mongoose');
	schemas = require('./schemas.js');

mongoose.model('User', schemas.userSchema);
var User = mongoose.model('User', schemas.userSchema),
 	Game = mongoose.model('Game', schemas.gameSchema);

mongoose.connect('localhost', 'dunk', function(err){
	if(err) throw err;
});


var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));

db.once('open', function callback() {

	var app = express();


	// Users
	app.get('/users', function(req, res) {
		User.find(function(err, users) {
			if (err)
			{
				console.log(err);
			}
			else
			{
				res.json(users);
			}
		});
	});

	app.get('/user/:id', function(req, res) {
		User.find({userId: req.params.id}, function(err, users) {
			if (err)
			{
				console.log(err);
			}
			else
			{
				res.json(users);
			}
		});
	});

	// Games
	app.get('/games', function(req, res) {
		Game.find(function(err, games) {
			if (err)
			{
				console.log(err);
			}
			else
			{
				res.json(games);
			}
		});
	});

	app.get('/game/:id', function(req, res) {
		Game.find({_id: mongoose.Types.ObjectId(req.params.id)}, function(err, games) {
			if (err)
			{
				console.log(err);
			}
			else
			{
				res.json(games);
			}
		});
	});
	app.listen(3000, "0.0.0.0");
	console.log('Seminole Tix REST Server listening on port 3000');
});
