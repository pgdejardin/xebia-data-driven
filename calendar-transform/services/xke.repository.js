import {marshallItem} from '@aws/dynamodb-data-marshaller';
import AWS from "aws-sdk";

const dynamodb = new AWS.DynamoDB();

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
        TableName: process.env.XKE_TABLE,
        Item: marshallItem(xkeSchema, xke)
    };

    await dynamodb.putItem(params).promise();
}
