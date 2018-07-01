data "aws_cloudformation_stack" "auth" {
    name = "${local.authorizer}"
}

data "aws_iam_policy_document" "auth_role" {
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

data "null_data_source" "auth" {
    inputs {
        lambda_arn = "${replace(data.aws_cloudformation_stack.auth.outputs.AuthorizerLambdaFunctionQualifiedArn, "/:[0-9]+$/", "")}"
    }
}

data "aws_iam_policy_document" "auth_policy" {
    statement {
        effect = "Allow"
        actions = [
            "lambda:InvokeFunction"
        ]
        resources = [
            "${data.null_data_source.auth.outputs.lambda_arn}"
        ]
    }
}

resource "aws_iam_role" "auth" {
    name = "${local.name}"
    assume_role_policy = "${data.aws_iam_policy_document.auth_role.json}"
}

resource "aws_iam_role_policy" "auth" {
    name = "default"
    role = "${aws_iam_role.auth.id}"
    policy = "${data.aws_iam_policy_document.auth_policy.json}"
}

resource "aws_api_gateway_authorizer" "auth" {
    name = "${local.name}"
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    authorizer_uri = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${data.null_data_source.auth.outputs.lambda_arn}/invocations"
    authorizer_credentials = "${aws_iam_role.auth.arn}"
}
