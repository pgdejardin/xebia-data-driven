terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

data "aws_iam_policy_document" "gateway_assume_role_policy" {
    statement {
        effect = "Allow"
        principals {
            type = "Service"
            identifiers = [
                "apigateway.amazonaws.com"
            ]
        }
        actions = [
            "sts:AssumeRole"
        ]
    }
}

resource "aws_iam_role" "gateway" {
    name = "${local.name}"
    assume_role_policy = "${data.aws_iam_policy_document.gateway_assume_role_policy.json}"
}

resource "aws_iam_role_policy_attachment" "api_gateway_cloud_watch_logs_policy" {
    role = "${aws_iam_role.gateway.id}"
    policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}

resource "aws_api_gateway_account" "gateway" {
    cloudwatch_role_arn = "${aws_iam_role.gateway.arn}"
}
