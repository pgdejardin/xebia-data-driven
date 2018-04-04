terragrunt = {

    remote_state {
        backend = "s3"
        config {
            encrypt = true
            region = "eu-west-1"
            bucket = "xdd-terraform-state.xebia.fr"
            key = "${path_relative_to_include()}"
            dynamodb_table = "xdd-terraform-state-lock.xebia.fr"
        }
    }

    terraform {
        extra_arguments "custom_vars" {
            commands = [
                "plan",
                "apply",
                "destroy",
            ]
            optional_var_files = [
                "${get_tfvars_dir()}/../../variables.tfvars",
                "${get_tfvars_dir()}/../variables.tfvars",
                "${get_tfvars_dir()}/variables.tfvars",
            ]
        }
    }

}
