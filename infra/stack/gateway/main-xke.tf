data "aws_cloudformation_stack" "xke_api" {
    name = "xdd-xke-api-${var.stage}"
}

resource "aws_api_gateway_resource" "xke" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_rest_api.gateway.root_resource_id}"
    path_part = "xke"
}

resource "aws_api_gateway_resource" "xke_v1" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_resource.xke.id}"
    path_part = "v1"
}

resource "aws_api_gateway_resource" "xke_v1_proxy" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_resource.xke_v1.id}"
    path_part = "{proxy+}"
}

resource "aws_api_gateway_method" "xke_v1_proxy" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.xke_v1_proxy.id}"
    http_method = "ANY"
    authorization = "CUSTOM"
    authorizer_id = "${aws_api_gateway_authorizer.auth.id}"
    request_parameters {
        "method.request.path.proxy" = true
    }
}

resource "aws_api_gateway_integration" "xke_v1_proxy" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.xke_v1_proxy.id}"
    http_method = "${aws_api_gateway_method.xke_v1_proxy.http_method}"
    type = "HTTP_PROXY"
    integration_http_method = "ANY"
    passthrough_behavior = "WHEN_NO_MATCH"
    uri = "${data.aws_cloudformation_stack.xke_api.outputs.ServiceEndpoint}/{proxy}"
    request_parameters {
        "integration.request.path.proxy" = "method.request.path.proxy"
    }
}

module "xke_v1_proxy_options" {
    source = "cors"
    api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.xke_v1_proxy.id}"
}
