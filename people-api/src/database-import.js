'use strict';

const database = require('./database');
const databaseSources = require('./database-sources');

module.exports = {
    importProcess,
};

async function importProcess() {

    const users = await databaseSources.findUserExport();
    const hrInfos = await databaseSources.findHrInfoExport();

    const hrInfoByEmail = listToMap(hrInfos, 'email');

    const peopleList = users.map(user => {
        let hrInfo = hrInfoByEmail[user.email];
        return merge(user, hrInfo);
    });

    console.log('People import sample', peopleList.slice(0, 3));

    await database.savePeopleList(peopleList);

    console.log('People import saved');

}

function merge(user, hrInfo) {

    let xebiaStartDate = hrInfo ? hrInfo.xebiaStartDate : null;
    let careerStartDate = hrInfo ? hrInfo.careerStartDate : null;

    return {
        email: user.email,
        givenName: user.givenName,
        familyName: user.familyName,
        xebia: {
            startDate: xebiaStartDate,
        },
        career: {
            startDate: careerStartDate,
        },
        google: {
            id: user.id,
            photoUrl: user.photoUrl,
        },
    };

}

function listToMap(list, field) {
    return list
        .filter(item => item[field] !== undefined)
        .reduce((map, item) => {
            map[item[field]] = item;
            return map;
        }, {});
}
