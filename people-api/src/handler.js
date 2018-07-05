'use strict';

const database = require('./database');
const databaseImport = require('./database-import');

module.exports = {
    listPeople,
    importPeople,
};

async function listPeople(event, context, callback) {

    try {

        const peopleList = await database.findPeopleList();

        const body = JSON.stringify(peopleList);

        callback(null, {
            statusCode: 200,
            headers: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Credentials': true,
            },
            body: body,
        });

    } catch (error) {

        console.error('People list has failed', error);

        callback(null, {
            statusCode: 404,
            headers: {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Credentials': true,
            }
        });

    }
}

async function importPeople() {

    try {

        console.log('People import started');

        await databaseImport.importProcess();

        console.log('People import finished');

    } catch (error) {

        console.error('People import failed', error);
        throw error;
    }

}
