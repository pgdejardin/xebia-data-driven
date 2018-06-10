terragrunt = {
    include {
        path = "${find_in_parent_folders()}"
    }
    terraform {
        source = "../../..///stack/gateway_account"
    }
}
