variable "region" {}

variable "project" {}
variable "project_tags" {
    type = "map"
}

variable "stage" {}
variable "stage_tags" {
    type = "map"
}

locals {
    tags = "${merge(var.project_tags, var.stage_tags)}"
}
