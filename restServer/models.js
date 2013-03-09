var crypto = require('crypto'),		// Cryptography library
    mongoose = require('mongoose'); // Mongoose MongoDB connector

// User
var userSchema = new mongoose.Schema(
	{
		userId: {
					type: String, 
					index: true, 
					unique: true, 
					sparse: true
				},
		hashedPassword: String,
		salt: String,
		pin: String,
		registered: Boolean,	
		name: {
				first: String, 
				last: String 
			  },
		cardNum: {
					type: String,
					index: true,
					unique: true
				 },
		email: {
					type: String,
					index: true,
					unique: true,
					sparse: true
			   },
		tickets: [{game_id: mongoose.Schema.ObjectId, confirmationId: String, seat: String, row: String}]
	});

// Virtual password property
userSchema.virtual('password').set(function(password) {

	this._password = password;
	this.salt = this.makeSalt();
	this.hashedPassword = this.encryptPassword(password);

}).get(function(){return this._password;});

// Create a random one-time salt
userSchema.methods.makeSalt = function() {
	return Math.round((new Date().valueOf() * Math.random())) + '';
};

// Encrypt password with SHA512 encryption with salt security
userSchema.methods.encryptPassword = function(password) {
	return crypto.createHmac('sha512', this.salt).update(password).digest('hex');
};

// Authenticate a user
userSchema.methods.authenticate = function(plainText) {
	return this.encryptPassword(plainText) === this.hashedPassword;
};

// Game
var gameSchema = new mongoose.Schema({

	sport: String,
	date: Date,
	availableDate: Date,
	teams: { 
				home: String, 
				away: String 
			},
	seats: Number,
	seatsLeft: Number,
	full: Boolean
});

// LoginToken
var loginTokenSchema = new mongoose.Schema({

	userId: { 
				type: String, 
				index: true 
			},
    series: { 
    			type: String, 
    			index: true 
    		},
    token: { 
    			type: String, 
    			index: true 
    		}
});

// Generate a token based on the date now
loginTokenSchema.methods.randomToken = function() {
	return Math.round((new Date().valueOf() * Math.random())) + '';
};

// Do this before saving
loginTokenSchema.pre('save', function(next) {

	// Get next token for security
	this.token = this.randomToken();

	// Generate a series token if it's a new token
	if (this.isNew)
		this.series = this.randomToken();

	next();
});

// Virtual cookieValue property
loginTokenSchema.virtual('cookieValue').get(function() {

	// Return a http acceptable cookie
	return JSON.stringify({ userId: this.userId, token: this.token, series: this.series });
});

// Register models with mongoose
var User = mongoose.model('User', userSchema),
 	Game = mongoose.model('Game', gameSchema),
 	LoginToken = mongoose.model('LoginToken', loginTokenSchema);

// Publicize models
exports.User = User;
exports.Game = Game;
exports.LoginToken = LoginToken;