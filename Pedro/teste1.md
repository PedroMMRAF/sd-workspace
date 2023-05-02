# 2018/19 - Teste 1 (Versão A)

## 1. a)

Faria sentido geo-replicar as fotografias de canais globais, permitindo acessos com menor latência a qualquer utilizador no mundo. Para qualquer tipo de canal, faria sentido replicar localmente, para prevenir que haja perda de dados, colocando as réplicas próximas de onde chegam os acessos.

## b)

## c)

Ao usar um serviço de CDN, seria possível aumentar a velocidade de carregamento de conteúdo estático por estas ficarem replicadas mais próximas dos utilizadores, neste caso, na periferia da network, nos diferentes ISPs, melhorando substancialmente a qualidade do serviço em termos de latência e velocidades de transferência. Deste modo, as fotografias teria tempos de carregamento bastante reduzidos ao tirar proveito deste serviço.
<br>

## 2.

### 1. F
### 2. F
### 3. F
### 4. V
### 5. F
### 6. F
### 7. V
### 8. F
### 9. V
### 10. V
### 11. F
### 12. V
### 13. V
### 14. V
### 15. V
### 16. F
### 17. V
<br>

## 3.

### 18. F
### 19. F
### 20. V
### 21. V
### 22. V
### 23. V
### 24. F
### 25. V
### 26. V
<br>

## 4.

### 27. V
### 28. F
### 29. F
### 30. F
### 31. V
### 32. F
### 33. V
### 34. V
<br>

## 5.

### 35. F
### 36. V
### 37. F
### 38. F
### 39. V
### 40. V
1. b in
2. c out
6. c in
5. a out
9. b out
10. a in
<br>
<br>

# 2021/22 - Teste 1 (Parte A)

## 1.

### 1. V
### 2. V
### 3. F
### 4. C
### 5. V
### 6. F
### 7. V
### 8. F
### 9. F
### 10. C
### 11. V
### 12. V
<br>

## 2.

### 13. V
### 14. V
### 15. F
### 16. V
### 17. V
### 18. C
### 19. V
### 20. V
### 21. F
### 22. V
### 23. F
### 24. V

## 3.

Web Sockets, Visto que precisamos enviar as nossas edições e receber as edições dos outros, a comunicação deve ser bidirecional. Este trata-se dum mecanismo bidirecional de alta performance, sendo então uma boa escolha.
<br>

## 4.

### a)
No caso de falha de comunicação, o cliente fica sem saber o seu identificador, ou se o objeto foi criado de todo. Sem saber que o objeto foi criado, é possivel que o cliente faça um pedido que leva à criação doutro objeto com id diferente e a mesma informação. Se o servidor retornasse 409 CONFLICT durante a criação do objeto com caracteristicas iguais, então o cliente ficaria sem saber o seu identificador. Um solução seria então devolver o id existente.

### b)

```java
var id = randomInt();
var user = new User(id, login, name);
try {
	var res = post("http://server.com/rest/user", user);
	if (res.statusCode == OK)
		return id;
} catch Timeout {
	var res = get("http://server.com/rest/user/" + id);
	if (res.statusCode == OK && user == res.getData())
		return id;
}
```
<br>

## 5.

### a)

Informação dos jogadores: Replicada apenas no centro de dados mais próximo geograficamente, visto que a informação dum jogador precisa apenas ser armazenada o mais próximo da sua localização possivel, com a sua informação replicada em pelo menos outro servidor para evitar perda de dados.

Informação dos objectos virtuais: Replicada apenas no centro de dados mais próximo geograficamente, pois esta informação não é necessária apresentar a todos os jogadores no mundo, apenas aqueles que se encontram mais próximos dos objetos. Mais uma vez, replicado em pelo menos outro servidor próximo para evitar a perda de dados.

### b)

Interagir com objeto virtual: A melhor escolha seria armazenar a sua informação num CDN, pois estes encontram-se na periferia da network, nos ISPs, e permitem acessos a vários clientes com latência mínima.

Atualizar posição do cliente: Neste caso seria no cliente, mas também poderia ser num CDN se quisesse saber a posição de outros clientes. Não sendo esse o caso, a cache pode estar simplesmente no cliente, e quando o cliente se mover, pode então atualizar localmente.

### c)

`http://roketon.go/rest/player/<player-id>?pos=<player-pos>` com a operação `PATCH`, sendo que é comum um serviço REST incluir no começo do seu caminho do URI o seu nome, seguido pelo nome da operação, e neste caso, para um especifico id, pretende-se atualizar apenas a sua posição. Também seria possível utilizar a operação `PUT`, especificando que se pretende atualizar apenas a posição do jogador.


# 2021/22 - Teste 1 (Parte B)

## P1.

```java
@PATH(BocasRest.PATH)
class BocasRest {
	static final String PATH = "/bocas"

	@GET
	@Path("/{bocaId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	byte[] ler_boca(
		@PathParam("bocaId") String bocaId
	);
	
	@POST
	@Path("/{autor}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	String mandar_boca(
		String boca,
		@PathParam("autor") String autor,
		@QueryParam("senha") String senha
	);

	@DELETE
	@Path("/{bocaId}")
	void apagar_boca(
		@PathParam("bocaId") String bocaId,
		@QueryParam("autor") String autor,
		@QueryParam("senha") String senha
	);
	
	@POST
	@Path("/{seguido}/seguidores/{seguidor}")
	void seguir(
		@PathParam("seguido") String seguido,
		@PathParam("seguidor") String seguidor,
		@QueryParam("senha") String senha
	);

	@DELETE
	@Path("/{seguido}/seguidores/{seguidor}")
	void deixar_de_seguir(
		@PathParam("seguido") String seguido,
		@PathParam("seguidor") String seguidor,
		@QueryParam("senha") String senha
	);

	@GET
	@Path("/{seguido}")
	@Produces(MediaType.APPLICATION_JSON)
	List<String> listar_bocas(
		@PathParam("seguido") String seguido,
		@QueryParam("user") String user,
		@QueryParam("senha") String senha
	);
}
```

## P2.

```java
@WebService(
	serviceName=BocasSoap.NAME,
	targetNamespace=BocasSoap.NAMESPACE,
	endpointInterface=BocasSoap.INTERFACE
)
public interface BocasSoap {
	static final String NAME = "bocas";
	static final String NAMESPACE = "http://bocas.com";
	static final String INTERFACE = "com.bocas.api.soap.BocasSoap";

	@WebFault
	public static class BocasException extends Exception {}

	@WebMethod
	byte[] ler_boca(String bocaId) throws BocasException;

	@WebMethod
	String mandar_boca(String boca, String autor, String senha) throws BocasException;

	@WebMethod
	void apagar_boca(String bocaId, String autor, String senha) throws BocasException;

	@WebMethod
	void seguir(String seguido, String seguidor, String senha) throws BocasException;

	@WebMethod
	void deixar_de_seguir(String seguido, String seguidor, String senha) throws BocasException;

	@WebMethod
	List<String >listar_bocas(String seguido, String user, String senha) throws BocasException;
}
```

## P3.

```java
public byte[] ler_boca(String bocaId, String user, String senha);
```
<br>
<br>

# 2020/21 - Teste 1 (Parte 1)

## 1.

### 1. F
### 2. V
### 3. F
### 4. A
### 5. V
### 6. V
### 7. A
### 8. V
### 9. V
### 10. C
### 11. F
### 12. F
<br>

## 2.

### 13. V
### 14. F
### 15. F
### 16. V
### 17. V
### 18. V
### 19. C
### 20. V
### 21. F
### 22. V
### 23. F
### 24. V
<br>

## 3.

### a)

Não, como a operação é idempotente, o resultado da invocação será sempre o mesmo independente do número de invocações. Se o cliente enviar a operação novamente, o servidor pode simplesmente correr o metodo outra vez para retornar o resultado.

### b)

Sim, o identificador da operação passaria a ser o timestamp e o identificador do cliente, que também é necessário ao usar números de sequencia.
TCP: Usar o timestamp chega.
UDP: Também seria necessário uma mensagem ter noção de qual era o timestamp da mensagem anterior, visto que as mensagens podem chegar fora de ordem, e visto que assim pode o servidor saber se falta alguma mensagem ou não.
<br>

## 4.

### a)

Guardar a informação de cada entidade nas réplicas mais próximas a sí, sendo que clientes de outras regiões provavelmente não necessitariam de marcar períodos numa entidade noutro local distante. Essa informação deve ser armazenada em pelo menos duas réplicas para previnir perda de dados. O mesmo se aplica às marcações em sí.

### b)

Um serviço CDN não seria muito adequado a este sistema, sendo que a informação varia bastate frequentemente, podendo-se esperar que esta não seja acedida tão frequentemente. O facto de ser necessário sincronizar tanta informação entre os varios PoP, dificilmente justifica a sua complexidade.

### c)

`http://reservationsforall.com/rest/entity/{entity_id}/{appointment_period}` com o método `POST`, especificando ao servidor que se pretende criar um recurso novo, neste caso, cria uma reserva no periodo dado. Este URL segue a convenção de começar o seu caminho por `rest`, e neste caso, encontramo-nos a aceder a uma entidade em especifico, com o intuito de reservar uma marcação no periodo dado.
<br>
<br>

# 2017/18 - Teste 1

## 2.

### a) F
### b) V
### c) F
### d) V
### e) V
### f) V
### g) F
### h) V
### i) F
### j) F
### k) F
### l) V
### m) V

## 3.

### a) F
### b) V
### c) V
### d) F
### e) F
### f) F
### g) V
### h) F

