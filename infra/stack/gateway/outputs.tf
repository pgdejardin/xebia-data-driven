output "gateway_invoke_url" {
    value = "${aws_api_gateway_deployment.gateway.invoke_url}"
}
