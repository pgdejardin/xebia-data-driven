'use strict';

jest.mock('./database');
jest.mock('./database-sources');

const databaseImport = require('./database-import');

const database = require('./database');
const databaseSources = require('./database-sources');

beforeEach(() => {
    database.savePeopleList.mockClear();
    databaseSources.findUserExport.mockClear();
    databaseSources.findHrInfoExport.mockClear();
});

test('should merge user and hrInfo', async () => {

    // given
    databaseSources.findUserExport.mockImplementation(() => Promise.resolve([user1, user2]));
    databaseSources.findHrInfoExport.mockImplementation(() => Promise.resolve([hrInfo1, hrInfo2]));

    // when
    await databaseImport.importProcess();

    // then
    await expect(database.savePeopleList.mock.calls[0][0]).toEqual([
        {
            email: "email1", givenName: "givenName1", familyName: "familyName1",
            xebia: {startDate: "date1.1"},
            career: {startDate: "date1.2"},
            google: {id: "1", photoUrl: "url1"},
        },
        {
            email: "email2", givenName: "givenName2", familyName: "familyName2",
            xebia: {startDate: "date2.1"},
            career: {startDate: "date2.2"},
            google: {id: "2", photoUrl: "url2"}
        },
    ]);

});

test('should use user and ignore missing hrInfo', async () => {

    // given
    databaseSources.findUserExport.mockImplementation(() => Promise.resolve([user3]));
    databaseSources.findHrInfoExport.mockImplementation(() => Promise.resolve([]));

    // when
    await databaseImport.importProcess();

    // then
    await expect(database.savePeopleList.mock.calls[0][0]).toEqual([
        {
            email: "email3", givenName: "givenName3", familyName: "familyName3",
            xebia: {startDate: null},
            career: {startDate: null},
            google: {id: "3", photoUrl: "url3"},
        }
    ]);

});

const user1 = {id: "1", email: "email1", givenName: "givenName1", familyName: "familyName1", photoUrl: "url1"};
const user2 = {id: "2", email: "email2", givenName: "givenName2", familyName: "familyName2", photoUrl: "url2"};
const user3 = {id: "3", email: "email3", givenName: "givenName3", familyName: "familyName3", photoUrl: "url3"};

const hrInfo1 = {email: "email1", xebiaStartDate: "date1.1", careerStartDate: "date1.2"};
const hrInfo2 = {email: "email2", xebiaStartDate: "date2.1", careerStartDate: "date2.2"};
