package br.com.sqs_ecommerce;

import br.com.sqs_ecommerce.dto.InfMessage;
import br.com.sqs_ecommerce.services.SQSService;
import java.time.LocalDateTime;

public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        System.out.println("Sistema de Simulação de ecommerce!");        
        System.out.println("Colocando pedido no Sistema:");
        
        // TODO - logica de colocar novos pedidos
        // InfMessage infMessage = new InfMessage("pedidos","transacao",123);
        // LocalDateTime now = LocalDateTime.now();
        // System.out.println("["+now+"] Pedido# " + infMessage);
        // SQSService.sendMessage(sqsClient, queueUrl, message);

        while(true){
            SQSService.messageReader();        
        }
    }
}

