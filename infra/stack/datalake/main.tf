terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

resource "aws_s3_bucket" "datalake" {
    bucket = "datalake-${var.stage}.${var.project}"
    tags = "${local.tags}"
}

resource "aws_s3_bucket" "datalake-tmp" {
    bucket = "datalake-tmp-${var.stage}.${var.project}"
    tags = "${local.tags}"
}
