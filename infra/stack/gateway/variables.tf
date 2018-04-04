variable "region" {}

variable "project" {}
variable "project_tags" {
    type = "map"
}

variable "stage" {}
variable "stage_tags" {
    type = "map"
}

variable "stack" {
    default = "gateway"
}

locals {
    name = "${var.stage}-${var.project}-${var.stack}"
    tags = "${merge(var.project_tags, var.stage_tags, map("Name", "${local.name}"))}"
}

variable "gateway_auto_deploy" {}
variable "gateway_xke_uri" {}
