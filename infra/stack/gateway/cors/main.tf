resource "aws_api_gateway_method" "resource_options" {
    rest_api_id = "${var.api_id}"
    resource_id = "${var.resource_id}"
    http_method = "OPTIONS"
    authorization = "NONE"
}

resource "aws_api_gateway_method_response" "resource_options" {
    rest_api_id = "${var.api_id}"
    resource_id = "${var.resource_id}"
    http_method = "${aws_api_gateway_method.resource_options.http_method}"
    status_code = 200
    response_models {
        "application/json" = "Empty"
    }
    response_parameters {
        "method.response.header.Access-Control-Allow-Headers" = true,
        "method.response.header.Access-Control-Allow-Methods" = true,
        "method.response.header.Access-Control-Allow-Origin" = true,
    }
}

resource "aws_api_gateway_integration" "resource_options" {
    rest_api_id = "${var.api_id}"
    resource_id = "${var.resource_id}"
    http_method = "${aws_api_gateway_method.resource_options.http_method}"
    type = "MOCK"
    request_templates {
        "application/json" = <<EOF
{
    "statusCode": 200
}
EOF
    }
}

resource "aws_api_gateway_integration_response" "resource_options" {
    rest_api_id = "${var.api_id}"
    resource_id = "${var.resource_id}"
    http_method = "${aws_api_gateway_method.resource_options.http_method}"
    status_code = "${aws_api_gateway_method_response.resource_options.status_code}"
    response_parameters = {
        "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,X-Authorization,X-Api-Key,X-Amz-Security-Token'",
        "method.response.header.Access-Control-Allow-Methods" = "'GET,OPTIONS,POST,PUT'",
        "method.response.header.Access-Control-Allow-Origin" = "'*'",
    }
}
