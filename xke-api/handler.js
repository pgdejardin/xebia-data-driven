import {findByKey, findXkeByYear} from './service/xke.repository';

export const findXke = async (event, context, callback) => {
    const year = event.pathParameters.year;
    const month = event.pathParameters.month;
    let result;
    if (month) {
        result = await findByKey(year, month);
    } else {
        result = await findXkeByYear(year);
    }

    if (result) {
        callback(null, {
            statusCode: 200,
            body: JSON.stringify(result),
        });
    } else {
        callback(null, {
            statusCode: 404,
            body: "Xke not found"
        });
    }
};
