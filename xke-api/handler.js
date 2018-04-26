import {pushXkeInDB, findByKey, findXkeByYear} from './service/xke.repository';

import {sanitizeFile, getFileFromBucket} from './service/file.service';

export const transform = async (event) => {
    const record = event.Records[0];
    const s3data = record.s3;
    const bucket = s3data.bucket.name;
    const inputFilename = decodeURIComponent(s3data.object.key);

    const [, year, month] = inputFilename.match(/^xke-calendar\/(\d{4})\/(\d{2})\/(\d{2})\/.*\.json/);

    const xkeFile = await getFileFromBucket(bucket, inputFilename);

    const slots = sanitizeFile(xkeFile);

    const xke = {year, month, slots};

    await pushXkeInDB(xke);
};

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
