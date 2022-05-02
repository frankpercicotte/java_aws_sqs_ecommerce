# java_aws_sqs_ecommerce
Projeto é em conjunto de estudo aplicação  sqs da aws, a ideia é simular o processo de compra de um produto, passando por (pedidos, transacao, nota fiscal, estoque e despacho). O projeto será rodados em máquinas diferentes, simulando microserviços que se comunicam por sqs.
Esta parte é responsável por tratar pedidos.

Difernça entre SNS e SQS aws:

 O SNS é um serviço de publicação de notificação (mensagens) podendo enviar para diversos canais (endpoints), podendo ser  para outra máquina, lambda, sms, email, ou até mesmo enviar para uM SQS...

Já o SQS é um serviço de mensageria, usando filas que ajuda garantir o controle da msg. Existindo dois modos padrão (não assíncrona) ou FIFO. É possível tmb controlar a quantidade de repetição de uma msg na fila, e tratando as não recebidas, ou com problemas enviando par um outo queue (fila).