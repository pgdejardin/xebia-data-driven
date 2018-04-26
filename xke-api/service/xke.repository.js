import {marshallItem, unmarshallItem} from '@aws/dynamodb-data-marshaller';
import AWS from "aws-sdk";

const dynamoDB = new AWS.DynamoDB();
const tableName = process.env.XKE_TABLE;

const xkeSchema = {
    year: {type: 'String', keyType: 'HASH'},
    month: {type: 'String', keyType: 'RANGE'},
    slots: {
        type: 'Collection'
    }
};

export async function pushXkeInDB(xke) {
    console.log('xke: ', xke);
    const params = {
        TableName: tableName,
        Item: marshallItem(xkeSchema, xke)
    };

    await dynamoDB.putItem(params).promise();
}

function unmarshallResult(data) {
    return data ? unmarshallItem(xkeSchema, data) : {};
}

export async function findByKey(year, month) {
    const params = {
        TableName: tableName,
        Key: {
            "year": {S: year},
            "month": {S: month}
        }
    };

    const xke = await dynamoDB.getItem(params).promise();
    return unmarshallResult(xke.Item);
}

export async function findXkeByYear(key) {
    const params = {
        TableName: tableName,
        ExpressionAttributeValues: {
            ":yr": {
                S: key
            }
        },
        ExpressionAttributeNames: {"#y": "year", "#m": "month"},
        KeyConditionExpression: "#y = :yr",
        ProjectionExpression: "#y,#m,slots"
    };

    const xkes = await dynamoDB.query(params).promise();
    return xkes.Items.map(d => unmarshallItem(xkeSchema, d));
}
