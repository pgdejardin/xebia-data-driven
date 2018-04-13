resource "aws_api_gateway_resource" "gateway_xke" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_rest_api.gateway.root_resource_id}"
    path_part = "xke"
}

resource "aws_api_gateway_resource" "gateway_xke_proxy" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    parent_id = "${aws_api_gateway_resource.gateway_xke.id}"
    path_part = "{proxy+}"
}

resource "aws_api_gateway_method" "gateway_xke_proxy" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.gateway_xke_proxy.id}"
    http_method = "ANY"
    authorization = "NONE"
    request_parameters {
        "method.request.path.proxy" = true
    }
}

resource "aws_api_gateway_integration" "gateway_xke_proxy" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    resource_id = "${aws_api_gateway_resource.gateway_xke_proxy.id}"
    http_method = "${aws_api_gateway_method.gateway_xke_proxy.http_method}"
    type = "HTTP_PROXY"
    integration_http_method = "ANY"
    passthrough_behavior = "WHEN_NO_MATCH"
    uri = "${var.gateway_xke_uri}/{proxy}"
    request_parameters {
        "integration.request.path.proxy" = "method.request.path.proxy"
    }
}
