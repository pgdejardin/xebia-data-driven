import {sanitizeFile, getFileFromBucket} from './services/file.service';
import {pushXkeInDB} from './services/xke.repository';

export const handler = async (event) => {
    const record = event.Records[0];
    const s3data = record.s3;
    const bucket = s3data.bucket.name;
    const inputFilename = decodeURIComponent(s3data.object.key);

    const [, year, month] = inputFilename.match(/^xke-calendar\/(\d{4})-(\d{2})\.json/);

    const xkeFile = await getFileFromBucket(bucket, inputFilename);

    const slots = sanitizeFile(xkeFile);

    const xke = {year, month, slots};

    await pushXkeInDB(xke);
};




