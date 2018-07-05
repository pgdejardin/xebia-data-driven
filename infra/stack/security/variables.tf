variable "stage" {}
variable "stage_tags" {
    type = "map"
}

variable "project" {}
variable "project_tags" {
    type = "map"
}

variable "stack" {
    default = "security"
}

locals {
    name = "${var.project}-${var.stack}-${var.stage}"
    tags = "${merge(var.stage_tags, var.project_tags, map("Name", "${local.name}"))}"
}

variable "aws_provider_version" {}
variable "region" {}
variable "bucket_name_suffix" {}
