package br.com.sqs_ecommerce;

import br.com.sqs_ecommerce.services.SQSService;

public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Sistema de Simulação de ecommerce!");
        System.out.println("Colocando pedido no Sistema:");

        // TODO - fase 2, criar logica de colocar novos pedidos.

        while (true) {
            SQSService.messageReader();
        }
    }
}
