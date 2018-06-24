terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

resource "aws_kms_key" "security" {
    enable_key_rotation = true
    deletion_window_in_days = 7
    tags = "${local.tags}"
}

resource "aws_kms_alias" "security" {
    name = "alias/${local.name}"
    target_key_id = "${aws_kms_key.security.key_id}"
}

resource "aws_ssm_parameter" "security" {
    type = "String"
    name = "/${var.project}/${var.stack}/kms-key-arn"
    value = "${aws_kms_key.security.arn}"
    tags = "${local.tags}"
}
