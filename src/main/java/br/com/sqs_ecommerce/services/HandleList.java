package br.com.sqs_ecommerce.services;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;

import br.com.sqs_ecommerce.dto.InfMessage;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;

public class HandleList {

    public static void messageReader() {

        GetQueueUrlRequest request = ConfigAws.getUrl();
        SqsClient sqsClient = ConfigAws.sqsClient();
        GetQueueUrlResponse createResult = sqsClient.getQueueUrl(request);

        List<Message> messages = SQSService.receiveMessages(sqsClient, createResult.queueUrl());

        for (Message mess : messages) {

            String myId = "pedidos";
            String jsonMess = mess.body();

            try {
                InfMessage pedido = new Gson().fromJson(jsonMess, InfMessage.class);

                String to = pedido.getTo();
                String from = pedido.getFrom();
                LocalDateTime now = LocalDateTime.now();

                // Monitorando as msg que estão sendo enviadas, so periodo de validacao.
                System.out.println("Mensagem recebida, from: " + pedido.getFrom() + " - to: " + pedido.getTo());

                if (to.equals(myId)) {
                    switch (from) {
                        case "transacao":
                            System.out.println(
                                    "[" + now + "][APROVADO TRANSACAO]" + " #Pedido: " + pedido.getNumPedido());
                            SQSService.deleteMessages(sqsClient, createResult.queueUrl(), mess);
                            break;
                        case "notaFiscal":
                            System.out.println("[" + now + "][NF EMITIDA]" + " #Pedido: " + pedido.getNumPedido());
                            SQSService.deleteMessages(sqsClient, createResult.queueUrl(), mess);
                            break;
                        case "estoque":
                            System.out.println("[" + now + "][ESTOQUE OK]" + " #Pedido: " + pedido.getNumPedido());
                            SQSService.deleteMessages(sqsClient, createResult.queueUrl(), mess);
                            break;
                        case "despacho":
                            System.out.println("[" + now + "][TRANSPORTADORA]" + " #Pedido: " + pedido.getNumPedido());
                            SQSService.deleteMessages(sqsClient, createResult.queueUrl(), mess);
                            // Só para gerar evento de tempo entre transporte e entrega.
                            pedido.setFrom(myId);
                            pedido.setTo(myId);
                            String jsonToPedido = new Gson().toJson(pedido);
                            SQSService.sendMessage(sqsClient, createResult.queueUrl(), jsonToPedido);
                            break;
                        case "pedidos":
                            // este case é para simular entrega do pedido
                            System.out.println("[" + now + "][ENTREGUE]" + " #Pedido: " + pedido.getNumPedido());
                            SQSService.deleteMessages(sqsClient, createResult.queueUrl(), mess);
                            break;
                        default:
                            break;
                    }
                }

            } catch (Exception e) {
                System.out.println("Error - msg mal formada -> " + mess.body());
            }
        }

        sqsClient.close();
    }
}
