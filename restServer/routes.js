var models = require('./models'),
	User = models.User,
	Game = models.Game,
	LoginToken = models.LoginToken;

exports.login = function(req, res) {
	if (req.session.userId)
	{
		res.json({success:false, error:'Already logged in.'});
	}
	else {
		if ((req.body.email || req.body.cardNum) && req.body.password) {
			var searchVal = req.body.email ? req.body.email : req.body.cardNum;
			User.findOne( {$or:[{email: searchVal}, {cardNum: searchVal}]}, function(err, user) {
				if (user && user.authenticate(req.body.password)) {
					req.session.userId = user.userId;

					// Remember me
					if (req.body.remember_me) {
						var loginToken = new LoginToken({ userId: user.userId });
						loginToken.save(function() {
							res.cookie('logintoken', loginToken.cookieValue, { expires: new Date(Date.now() + 2 * 604800000), path: '/' });
							res.json({success:true});
						});
					} else {
						res.json({success:true});
					}

				} else {
					res.json({success:false, error:'Invalid email/cardNum or password.'});
				}
			});
		} else {
			res.json({success:false, error:'Login reqiures email|cardNum & password'});
		}
	}
};

exports.logout = function(req, res) {
	if (req.session) {
		LoginToken.remove({ userId: req.currentUser.userId }, function() {});
		res.clearCookie('logintoken');
		req.session.destroy(function() {});
		res.json({success:true});
	}
	else
		res.json({success:false, error:'Not logged in.'});
};

exports.users_register = function(req, res) {
	if (req.session.userId) {
		res.json({success:false, error:'Already logged in. No need to register'});
	}
	else {
		// Require exisiting cardNum, pin, email, and password
		if (req.body.cardNum && req.body.pin && req.body.email && req.body.password) {
			User.findOne({cardNum: req.body.cardNum}, function(err, user) {
				if (user && !user.registered) {
					if (req.body.pin == user.pin) {
						if (check(req.body.email).isEmail()) {
							user.userId = req.body.email.match(/[^@]+/)[0];
							user.password = req.body.password;
							user.email = req.body.email;
							user.registered = true;
							user.save(function(error) {
								if (error) {
									res.json({success: false, message: error.err})
								}
								else
									res.json({success: true})
							});
						}
						else {
							res.json({success: false, message: 'Invalid email.'});
						}
					}
					else {
						res.json({success: false, message: 'Invalid pin.'});
					}						
					
				} else {
					if (!user)
						res.json({success: false, message: 'No user found with that card number'});
					else 
						res.json({success: false, message: 'User already registered'});
				}
			});
		}
		else {
			res.json({success: false, message: 'Missing arguments. Need cardNum, pin, desired email, and desired password'});
		}
	}
};

exports.users_view = function(req, res) {
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
};

exports.user_view = function(req, res) {
	res.json(req.currentUser);
};

exports.games_view = function(req, res) {
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
};

exports.game_view = function(req, res) {
	Game.findOne({_id: mongoose.Types.ObjectId(req.params.id)}, function(err, game) {
		if (err)
		{
			console.log(err);
			res.json(err);

		}
		else
		{
			// Mongoose docs are wrapped and not mutable for purposes of being serialized
			game = game.toJSON();
			game.full = game.seatsLeft == 0 ? true : false;
			delete game.seatsLeft;

			res.json(game);
		}
	});
};

exports.game_reserve = function(req, res) {
	Game.findOne({_id: mongoose.Types.ObjectId(req.params.id)}, function (err, game) {
		if (err)
		{
			console.log(err);
			res.json(err);
		}
		else
		{
			if (game.seatsLeft == 0)
			{
				res.json(
				{
					success: false,
					message: 'Game is full'
				});
			}
			// Check if user has already reserved a ticket
			else
			{
				for (ticket in req.currentUser.tickets)
				{
					if (game._id.equals(req.currentUser.tickets[ticket].game_id)){
						res.json(
						{
							success: false,
							message: 'Already have a ticket for this game'
						});
						return;
					}
				}

				game.seatsLeft -= 1;
				game.save();
				var newTicket = {
					game_id: game._id,
					confirmationId: '751263',
					seat: 'B',
					row: '42'
				};
				var newTickets = req.currentUser.tickets;
				newTickets.push(newTicket);
				req.currentUser.tickets = newTickets;
				req.currentUser.save();
				res.json(
				{
					success: true
				});
			}
		}
	});
};