terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

provider "aws" {
    alias = "acm"
    region = "${var.region_acm}"
}

data "aws_route53_zone" "zone" {
    name = "${var.zone_name}"
}

data "aws_acm_certificate" "certificate" {
    provider = "aws.acm"
    domain = "${var.zone_name}"
}

resource "aws_cloudwatch_log_group" "gateway" {
    name = "API-Gateway-Execution-Logs_${aws_api_gateway_rest_api.gateway.id}/${var.stage}"
    retention_in_days = "14"
}

resource "aws_api_gateway_rest_api" "gateway" {
    name = "${local.name}"
    binary_media_types = [
        "application/octet-stream",
        "image/jpeg",
    ]
}

resource "aws_api_gateway_deployment" "gateway" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    stage_name = "${var.stage}"
    stage_description = "${sha1(file("./main.tf"))}"
    depends_on = [
        # one method must exist before deployment, otherwise unsuccessful
        "aws_api_gateway_method.xke_v1_proxy"
    ]
    lifecycle {
        create_before_destroy = true
    }
}

resource "aws_api_gateway_domain_name" "gateway" {
    domain_name = "${var.zone_name}"
    certificate_arn = "${data.aws_acm_certificate.certificate.arn}"
}

resource "aws_api_gateway_base_path_mapping" "gateway" {
    api_id = "${aws_api_gateway_rest_api.gateway.id}"
    stage_name = "${aws_api_gateway_deployment.gateway.stage_name}"
    domain_name = "${aws_api_gateway_domain_name.gateway.domain_name}"
}

resource "aws_api_gateway_method_settings" "gateway" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    stage_name  = "${aws_api_gateway_deployment.gateway.stage_name}"
    method_path = "*/*"
    settings {
        logging_level   = "ERROR"
    }
}

resource "aws_route53_record" "gateway" {
    zone_id = "${data.aws_route53_zone.zone.id}"
    name = "${var.zone_name}"
    type = "A"
    alias {
        zone_id = "${aws_api_gateway_domain_name.gateway.cloudfront_zone_id}"
        name = "${aws_api_gateway_domain_name.gateway.cloudfront_domain_name}"
        evaluate_target_health = true
    }
}
