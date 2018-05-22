terragrunt = {

    remote_state {
        backend = "s3"
        config {
            encrypt = true
            region = "eu-west-1"
            key = "${path_relative_to_include()}"
            bucket = "xdd-terraform-state-dev.xebia.fr"
            dynamodb_table = "xdd-terraform-state-lock-dev"
        }
    }

    terraform {
        extra_arguments "custom_vars" {
            commands = [
                "${get_terraform_commands_that_need_vars()}"
            ]
            optional_var_files = [
                "${get_tfvars_dir()}/../../variables.tfvars",
                "${get_tfvars_dir()}/../variables.tfvars",
                "${get_tfvars_dir()}/variables.tfvars",
            ]
        }
    }

}
