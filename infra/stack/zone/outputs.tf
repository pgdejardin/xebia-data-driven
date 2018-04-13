output "zone_id" {
    value = "${aws_route53_zone.zone.id}"
}

output "zone_name" {
    # remove trailing '.' eg. "foo.bar." => "foo.bar"
    value = "${replace(aws_route53_zone.zone.name, "/[.]$/", "")}"
}

output "certificate_arn" {
    value = "${aws_acm_certificate.zone.arn}"
}
