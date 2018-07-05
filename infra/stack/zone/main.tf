terraform {
    backend "s3" {}
}

provider "aws" {
    version = "${var.aws_provider_version}"
    region = "${var.region}"
}

resource "aws_route53_zone" "zone" {
    name = "${var.zone_name}"
    comment = "Public zone for ${var.zone_name}"
    force_destroy = false
    tags = "${local.tags}"
}
