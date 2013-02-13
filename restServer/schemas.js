mongoose = require('mongoose');

var userSchema = new mongoose.Schema({
	userId: String,
	password: String,
	name: { first: String, last: String },
	studentId: String,
	email: String,
	tickets: [{game_id: mongoose.Schema.ObjectId, confirmationId: String, seat: String, row: String}]
});

var gameSchema = new mongoose.Schema({
	sport: String,
	date: Date,
	availableDate: Date,
	teams: { home: String, away: String },
	seats: Number,
	seatsLeft: Number
});

exports.userSchema = userSchema;
exports.gameSchema = gameSchema;