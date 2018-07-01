terraform {
    backend "s3" {}
}

provider "aws" {
    region = "${var.region}"
}

resource "aws_s3_bucket" "datalake" {
    bucket = "${local.name}.${var.bucket_name_suffix}"
    tags = "${local.tags}"
}

resource "aws_s3_bucket_object" "raw" {
    bucket = "${aws_s3_bucket.datalake.bucket}"
    key = "/raw/readme.txt"
    content_type = "text/plain"
    content = <<EOF
# RAW
This bucket location should only contain raw data extracts organised by extraction date.
- /raw/{source}/{year}/{month}/{day}/extract.json
- ...
EOF
}
