terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

resource "aws_api_gateway_rest_api" "gateway" {
    name = "gateway-${var.stage}.${var.project}"
}

resource "aws_api_gateway_deployment" "gateway" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    stage_name = "${var.stage}"
    # use timestamp to force re-deployment when enabled
    stage_description = "${var.gateway_auto_deploy == "true" ? timestamp() : var.stage}"
    depends_on = [
        # one method must exist before deployment, otherwise unsuccessful
        "aws_api_gateway_method.gateway_xke_proxy"
    ]
}

#
# XKE
#

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
