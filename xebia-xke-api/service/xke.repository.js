import {unmarshallItem} from '@aws/dynamodb-data-marshaller';
import AWS from "aws-sdk";

const dynamodb = new AWS.DynamoDB();

const xkeSchema = {
    year: {type: 'String', keyType: 'HASH'},
    month: {type: 'String', keyType: 'RANGE'},
    slots: {
        type: 'Collection'
    }
};

function unmarshallResult(data) {
    return data ? unmarshallItem(xkeSchema, data) : {};
}

export async function findByKey(year, month) {
    const params = {
        TableName: process.env.XKE_TABLE,
        Key: {
            "year": {S: year},
            "month": {S: month}
        }
    };

    const xke = await dynamodb.getItem(params).promise();
    return unmarshallResult(xke.Item);
}

export async function findXkeByYear(key) {
    const params = {
        TableName: process.env.XKE_TABLE,
        ExpressionAttributeValues: {
            ":yr": {
                S: key
            }
        },
        ExpressionAttributeNames: {"#y": "year", "#m": "month"},
        KeyConditionExpression: "#y = :yr",
        ProjectionExpression: "#y,#m,slots"
    };

    const xkes = await dynamodb.query(params).promise();
    return xkes.Items.map(d => unmarshallItem(xkeSchema, d));
}
