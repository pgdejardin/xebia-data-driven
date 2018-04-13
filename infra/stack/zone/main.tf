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

resource "aws_route53_zone" "zone" {
    name = "${var.zone_name}"
    comment = "Public zone for ${var.zone_name}"
    force_destroy = false
    tags = "${local.tags}"
}

resource "aws_route53_record" "zone" {
    name = "${aws_acm_certificate.zone.domain_validation_options.0.resource_record_name}"
    type = "${aws_acm_certificate.zone.domain_validation_options.0.resource_record_type}"
    zone_id = "${aws_route53_zone.zone.id}"
    records = [
        "${aws_acm_certificate.zone.domain_validation_options.0.resource_record_value}"
    ]
    ttl = 60
}

resource "aws_acm_certificate" "zone" {
    provider = "aws.acm"
    domain_name = "${var.zone_name}"
    subject_alternative_names = [
        "*.${var.zone_name}"
    ]
    validation_method = "DNS"
    tags = "${local.tags}"
}

resource "aws_acm_certificate_validation" "zone" {
    provider = "aws.acm"
    certificate_arn = "${aws_acm_certificate.zone.arn}"
    validation_record_fqdns = [
        "${aws_route53_record.zone.fqdn}"
    ]
}
