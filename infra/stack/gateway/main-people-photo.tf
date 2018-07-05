data "aws_s3_bucket" "datalake" {
    bucket = "${var.project}-datalake-${var.stage}.xebia.fr"
}

resource "aws_api_gateway_resource" "people_v1_id" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_resource.people_v1.id}"
    path_part = "{email}"
}

resource "aws_api_gateway_resource" "people_v1_id_photo" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_resource.people_v1_id.id}"
    path_part = "photo"
}

resource "aws_api_gateway_method" "people_v1_id_photo" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1_id_photo.id}"
    http_method = "GET"
    authorization = "CUSTOM"
    authorizer_id = "${aws_api_gateway_authorizer.auth.id}"
    request_parameters {
        "method.request.path.email" = true
    }
}

resource "aws_api_gateway_integration" "people_v1_id_photo" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1_id_photo.id}"
    http_method = "${aws_api_gateway_method.people_v1_id_photo.http_method}"
    type = "AWS"
    integration_http_method = "GET"
    uri = "arn:aws:apigateway:${var.region}:s3:path/${data.aws_s3_bucket.datalake.bucket}/raw/picture-drive/{email}.jpg"
    credentials = "${aws_iam_role.people_photo_role.arn}"
    request_parameters {
        "integration.request.path.email" = "method.request.path.email"
    }
}

resource "aws_api_gateway_method_response" "people_v1_id_photo" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1_id_photo.id}"
    http_method = "${aws_api_gateway_integration.people_v1_id_photo.http_method}"
    status_code = "200"
    response_parameters = {
        "method.response.header.Content-Type" = true
        "method.response.header.Content-Length" = true
    }
    response_models {
        "image/jpeg" = "Empty"
    }
}

resource "aws_api_gateway_integration_response" "people_v1_id_photo" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1_id_photo.id}"
    http_method = "${aws_api_gateway_method.people_v1_id_photo.http_method}"
    status_code = "${aws_api_gateway_method_response.people_v1_id_photo.status_code}"
    content_handling = "CONVERT_TO_BINARY"
    response_parameters {
        "method.response.header.Content-Type" = "integration.response.header.Content-Type"
        "method.response.header.Content-Length" = "integration.response.header.Content-Length"
    }
}

data "aws_iam_policy_document" "people_photo_assume_role_policy" {
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

data "aws_iam_policy_document" "people_photo_policy" {
    statement {
        effect = "Allow"
        actions = [
            "s3:GetObject"
        ]
        resources = [
            "${data.aws_s3_bucket.datalake.arn}/raw/picture-drive/*"
        ]
    }
}

resource "aws_iam_role" "people_photo_role" {
    name = "${local.name}-people"
    assume_role_policy = "${data.aws_iam_policy_document.people_photo_assume_role_policy.json}"
}

resource "aws_iam_role_policy" "people_photo_role_policy" {
    name = "${local.name}-people"
    role = "${aws_iam_role.people_photo_role.id}"
    policy = "${data.aws_iam_policy_document.people_photo_policy.json}"
}
