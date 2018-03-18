terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

resource "aws_s3_bucket" "datalake" {
    bucket = "datalake-${var.environment}.${var.project}"
    tags = "${local.tags}"
}
