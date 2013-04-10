conn = new Mongo();
db = conn.getDB("dunk");

var user = {
	pin: '2313',
	registered: false,
	name: {first: 'Brad', last: 'Pittson'},
	cardNum: "812361273651"
};

db.users.insert(user);

var user = {
	pin: '9092',
	registered: false,
	name: {first: 'Ted', last: 'Maybeen'},
	cardNum: "812361211651"
};

db.users.insert(user);

var user = {
	pin: '8862',
	registered: false,
	name: {first: 'Stella', last: 'Chicotsky'},
	cardNum: "822361273651"
};

db.users.insert(user);

var user = {
	pin: '3247',
	registered: false,
	name: {first: 'Michael', last: 'Bean'},
	cardNum: "812361773651"
};

db.users.insert(user);

var user = {
	pin: '8745',
	registered: false,
	name: {first: 'Joel', last: 'Jakim'},
	cardNum: "812881273651"
};

db.users.insert(user);

var game = {
	availableDate: "4/9/2013",
	date: "5/18/2013",
	seats: 100,
	seatsLeft: 100,
	sport: "football",
	teams: {
		home: "fsu",
		away: "ucf"
	}
};

db.games.insert(game);

var game = {
	availableDate: "4/9/2013",
	date: "5/25/2013",
	seats: 100,
	seatsLeft: 100,
	sport: "football",
	teams: {
		home: "fsu",
		away: "uf"
	}
};

db.games.insert(game);

var game = {
	availableDate: "4/9/2013",
	date: "6/1/2013",
	seats: 100,
	seatsLeft: 100,
	sport: "football",
	teams: {
		home: "fsu",
		away: "usf"
	}
};

db.games.insert(game);

var game = {
	availableDate: "4/9/2013",
	date: "5/1/2013",
	seats: 100,
	seatsLeft: 0,
	sport: "football",
	teams: {
		home: "fsu",
		away: "unf"
	}
};

db.games.insert(game);

var game = {
	availableDate: "4/9/2013",
	date: "5/8/2013",
	seats: 100,
	seatsLeft: 0,
	sport: "football",
	teams: {
		home: "fsu",
		away: "mit"
	}
};

db.games.insert(game);