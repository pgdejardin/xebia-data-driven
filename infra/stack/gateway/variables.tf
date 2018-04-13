variable "stage" {}
variable "stage_tags" {
    type = "map"
}

variable "project" {}
variable "project_tags" {
    type = "map"
}

variable "stack" {
    default = "gateway"
}

locals {
    name = "${var.stage}-${var.project}-${var.stack}"
    tags = "${merge(var.stage_tags, var.project_tags, map("Name", "${local.name}"))}"
}

variable "region" {}

variable "state_bucket" {}
variable "zone_prefix" {}
