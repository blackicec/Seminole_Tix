mongoose = require('mongoose');
var crypto = require('crypto');

var userSchema = new mongoose.Schema({
	userId: {type: String, index: true, unique: true, sparse: true},
	hashedPassword: String,
	salt: String,
	pin: String,
	registered: Boolean,	
	name: { first: String, last: String },
	cardNum: {type: String, index: true, unique: true},
	email: {type: String, index: true, unique: true, sparse: true},
	tickets: [{game_id: mongoose.Schema.ObjectId, confirmationId: String, seat: String, row: String}]
});

userSchema.virtual('password').set(function(password) {
	this._password = password;
	this.salt = this.makeSalt();
	this.hashedPassword = this.encryptPassword(password);
}).get(function(){return this._password;});

userSchema.methods.makeSalt = function() {
	return Math.round((new Date().valueOf() * Math.random())) + '';
};
userSchema.methods.encryptPassword = function(password) {
	return crypto.createHmac('sha1', this.salt).update(password).digest('hex');
};
userSchema.methods.authenticate = function(plainText) {
	return this.encryptPassword(plainText) === this.hashedPassword;
};

var loginTokenSchema = new mongoose.Schema({
	userId: { type: String, index: true },
    series: { type: String, index: true },
    token: { type: String, index: true }
});

loginTokenSchema.method('randomToken', function() {
	return Math.round((new Date().valueOf() * Math.random())) + '';
});

loginTokenSchema.pre('save', function(next) {

	this.token = this.randomToken();

	if (this.isNew)
		this.series = this.randomToken();

	next();
});

loginTokenSchema.virtual('id').get(function() {
	return this._id.toHexString();
});

loginTokenSchema.virtual('cookieValue').get(function() {
	return JSON.stringify({ userId: this.userId, token: this.token, series: this.series });
});

var gameSchema = new mongoose.Schema({
	sport: String,
	date: Date,
	availableDate: Date,
	teams: { home: String, away: String },
	seats: Number,
	seatsLeft: Number,
	full: Boolean
});

var User = mongoose.model('User', userSchema),
 	Game = mongoose.model('Game', gameSchema),
 	LoginToken = mongoose.model('LoginToken', loginTokenSchema);

exports.User = User;
exports.Game = Game;
exports.LoginToken = LoginToken;