terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

data "terraform_remote_state" "zone" {
    backend = "s3"
    config {
        region = "${var.region}"
        bucket = "${var.state_bucket}"
        key = "zone"
    }
}

resource "aws_api_gateway_rest_api" "gateway" {
    name = "${local.name}"
}

resource "aws_api_gateway_deployment" "gateway" {
    rest_api_id = "${aws_api_gateway_rest_api.gateway.id}"
    stage_name = "${var.stage}"
    stage_description = "${sha1(file("./main.tf"))}"
    depends_on = [
        # one method must exist before deployment, otherwise unsuccessful
        "aws_api_gateway_method.xke_proxy"
    ]
    lifecycle {
        create_before_destroy = true
    }
}

resource "aws_api_gateway_domain_name" "gateway" {
    domain_name = "${var.zone_prefix}${data.terraform_remote_state.zone.zone_name}"
    certificate_arn = "${data.terraform_remote_state.zone.certificate_arn}"
}

resource "aws_api_gateway_base_path_mapping" "gateway" {
    api_id = "${aws_api_gateway_rest_api.gateway.id}"
    stage_name = "${aws_api_gateway_deployment.gateway.stage_name}"
    domain_name = "${aws_api_gateway_domain_name.gateway.domain_name}"
}

resource "aws_route53_record" "gateway" {
    zone_id = "${data.terraform_remote_state.zone.zone_id}"
    name = "${var.zone_prefix}${data.terraform_remote_state.zone.zone_name}"
    type = "A"
    alias {
        zone_id = "${aws_api_gateway_domain_name.gateway.cloudfront_zone_id}"
        name = "${aws_api_gateway_domain_name.gateway.cloudfront_domain_name}"
        evaluate_target_health = true
    }
}
