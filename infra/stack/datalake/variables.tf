variable "region" {}

variable "project" {}
variable "project_tags" {
    type = "map"
}

variable "environment" {}
variable "environment_tags" {
    type = "map"
}

locals {
    tags = "${merge(var.project_tags, var.environment_tags)}"
}
