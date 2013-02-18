conn = new Mongo();
db = conn.getDB("dunk");

for (var i = 0; i < 100; i++)
{
    var user = {
        pin: '1234',
        registered: false,
        name: {first: 'John', last: 'Doe'},
        cardNum: i.toString()
    };
    db.users.insert(user);
}
