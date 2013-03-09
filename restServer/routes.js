var models = require('./models'),		// Mongoose models required for database manipulation
	check = require('validator').check;	// Email validator

// Mongoose Models
var	User = models.User,					// Users Collection in MongoDB
	Game = models.Game,					// Games Collection in MongoDB
	LoginToken = models.LoginToken;		// LoginTokens Collection in MongoDB

// Login
/*
	REST Method: POST
	Purpose: Logs a user in to the system 
	Requires: email (or cardNum) and password 
	Returns: 
		Success: Success Message <JSON> 
		Failure: Error Message <JSON> 
*/
exports.login = function(req, res) {

	// Registered users need not login
	if (req.session.userId)
		res.json(
			{
				success:false, 
				message:'Already logged in.'
			});

	else {

		// Check all arguments were received
		if ((req.body.email || req.body.cardNum) && req.body.password) {

			// Check how we are logging in (email or cardNum)
			var searchVal = req.body.email ? req.body.email : req.body.cardNum;

			// Search  for user by searchVal
			User.findOne( {$or:[{email: searchVal}, {cardNum: searchVal}]}, function(err, user) {

				// Check if user was found and if so, do the passwords match?
				if (user && user.authenticate(req.body.password)) {

					// Register user in session
					req.session.userId = user.userId;

					// Remember me was chosen
					if (req.body.remember_me) {

						// Create and store a token/cookie
						var loginToken = new LoginToken({ userId: user.userId });
						loginToken.save(function() {
							res.cookie('logintoken', loginToken.cookieValue, { expires: new Date(Date.now() + 2 * 604800000), path: '/' });
						});

					}

					res.json(
						{
							success:true
						});
				} else 

					// User doesn't exist or bad password but we give a generic message
					// for security purposes
					res.json(
						{
							success:false,
						 	message:'Invalid email/card number or password'
						});

			});

		} else

			res.json(
				{
					success:false, 
					message:'Login reqiures email|cardNum & password'
				});
	}
};

// Logout
/*
	REST Method: GET
	Purpose: Log a user out of the system
	Returns:
		Success: Success Message <JSON>
		Failure: Error Message <JSON>
	Notes: 
		Clears the coookie and destroys the session as well
*/
exports.logout = function(req, res) {

	// Check if session exists
	if (req.session) {

		// Destroy session/cookie
		LoginToken.remove({ userId: req.currentUser.userId }, function() {});
		res.clearCookie('logintoken');
		req.session.destroy(function() {});
		res.json(
			{
				success:true
			});

	} else

		res.json(
			{
				success:false,
				message:'Not logged in'
			});
};

// User Registration
/*
	REST Method: POST
	Purpose: Attempts to register a user.
	Requires: cardNum, pin, email, and password
	Returns:
		Success: Success Message <JSON>
		Failure: Error Message <JSON>
	Notes:
		Email validation is performed.
		Prevents duplicate user registration.
*/
exports.users_register = function(req, res) {

	// Check if user is logged in
	if (req.session.userId)

		res.json(
			{
				success:false,
				message:'Already logged in. No need to register'
			});

	else {

		// Require exisiting cardNum, pin, email, and password
		if (req.body.cardNum && req.body.pin && req.body.email && req.body.password) {

			// Search for user by cardNum
			User.findOne({cardNum: req.body.cardNum}, function(err, user) {

				// Check that a user was found and that they aren't registered
				if (user && !user.registered) {

					// Given PIN number must match
					if (req.body.pin == user.pin) {

						// Validate email before registration
						if (check(req.body.email).isEmail()) {

							user.userId = req.body.email.match(/[^@]+/)[0];
							user.password = req.body.password;
							user.email = req.body.email;
							user.registered = true;
							user.save(function(error) {

								if (error)

									// MongoDB Error
									res.json(
										{
											success: false,
											message: error.err
										});
								else
									res.json(
										{
											success: true
										});

							});

						} else
							res.json(
								{
									success: false, 
									message: 'Invalid email format'
								});

					} else

						// Generic error message for security purposes
						res.json(
							{
								success: false, 
								message: 'Invalid card number or PIN'
							});					
					
				} else

					if (!user)

						// Generic error message for security purposes
						res.json(
							{
								success: false, 
								message: 'Invalid card number or PIN'
							});
					else 
						res.json(
							{
								success: false, 
								message: 'Account already exists'
							});

			});

		} else 
			res.json(
				{
					success: false, 
					message: 'Missing arguments. Need cardNum, pin, desired email, and desired password'
				});
	}
};

// View Users
/*
	REST Method: GET
	Purpose: Gets the list of users in the system, registered or not
	Returns:
		Success: User Model <JSON>[]
		Failure: Error Message <JSON>
	Note: 
		This method is to be removed as soon production starts
		as it is inherently insecure but necessary for development.
*/
exports.users_view = function(req, res) {

	// Find all users
	User.find(function(err, users) {

		if (err) {

			// MongoDB Error
			console.log(err);
			res.json(
				{
					success:false, 
					message: err
				});

		} else
			res.json(users);
	});
};

// View User
/*
	REST Method: GET
	Purpose: Gets the currently logged in user
	Returns:
		Success: User Model <JSON>
*/
exports.user_view = function(req, res) {

	// currentUser comes from loadUser defined in lib.js
	res.json(req.currentUser);
};

// View Games
/*
	REST Method: GET
	Purpose: Gets the list of games in the database
	Returns:
		Success: Game Model <JSON>[]
		Failure: Error Message <JSON>
*/
exports.games_view = function(req, res) {

	// Find all games
	Game.find(function(err, games) {

		if (err) {

			// MongoDB Error
			console.log(err);
			res.json(
				{
					success:false, 
					message: err
				});

		} else
			res.json(games);
	});
};

// View Game
/*
	REST Method: GET
	Purpose: Gets the game selected from the database
	Requires: ObjectID <String> (of game)
	Returns:
		Success: Game Model <JSON>
		Failure: Error Message <JSON>
*/
exports.game_view = function(req, res) {

	// Find game by ObjectID
	Game.findOne({_id: mongoose.Types.ObjectId(req.params.id)}, function(err, game) {

		if (err) {

			console.log(err);
			res.json(
				{
					success:false, 
					message: err
				});

		}
		else {

			// Don't give out seatsLeft info
			// Note: Mongoose docs are wrapped and not mutable for purposes of being serialized
			game = game.toJSON();
			game.full = game.seatsLeft == 0 ? true : false;
			delete game.seatsLeft;

			res.json(game);
		}
	});
};

// Reserve Ticket
/*
	REST Method: POST
	Purpose: Attempts to reserve a ticket to the selected game
	Requires: ObjectID <String> (of game)
	Returns:
		Success: Success Message <JSON>
		Failure: Error Message <JSON>
*/
exports.game_reserve = function(req, res) {

	// Find game by ObjectID
	Game.findOne({_id: mongoose.Types.ObjectId(req.params.id)}, function (err, game) {

		if (err) {

			// MongoDB Error
			console.log(err);
			res.json(
				{
					success:false,
					 message: err
				});

		} else {

			// Check if game is full
			if (game.seatsLeft == 0) {

				res.json(
					{
						success: false,
						message: 'Game is full'
					});

			} else {

				// Check if user has already reserved a ticket
				for (ticket in req.currentUser.tickets) {

					if (game._id.equals(req.currentUser.tickets[ticket].game_id)) {

						res.json(
							{
								success: false, 
								message: 'Already have a ticket for this game'
							});
						return;

					}

				}

				// Reserve ticket
				game.seatsLeft -= 1;
				game.save();

				// Add a new ticket to the user
				// Use magic confirmation number
				var newTicket = {
					game_id: game._id,
					confirmationId: '751263',
				};
				var newTickets = req.currentUser.tickets;

				// Modify user
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
