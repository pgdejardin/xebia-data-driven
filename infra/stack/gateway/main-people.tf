data "aws_cloudformation_stack" "people_api" {
    name = "xdd-people-api-${var.stage}"
}

resource "aws_api_gateway_resource" "people" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_rest_api.gateway.root_resource_id}"
    path_part = "people"
}

resource "aws_api_gateway_resource" "people_v1" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_resource.people.id}"
    path_part = "v1"
}

resource "aws_api_gateway_method" "people_any" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1.id}"
    http_method = "ANY"
    authorization = "CUSTOM"
    authorizer_id = "${aws_api_gateway_authorizer.auth.id}"
    request_parameters {
        "method.request.path.proxy" = true
    }
}

resource "aws_api_gateway_integration" "people_any" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1.id}"
    http_method = "${aws_api_gateway_method.people_any.http_method}"
    type = "HTTP_PROXY"
    integration_http_method = "ANY"
    passthrough_behavior = "WHEN_NO_MATCH"
    uri = "${data.aws_cloudformation_stack.people_api.outputs.ServiceEndpoint}/{proxy}"
    request_parameters {
        "integration.request.path.proxy" = "method.request.path.proxy"
    }
}

module "people_v1_options" {
    source = "cors"
    api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.people_v1.id}"
}
