#!/usr/local/bin/groovy
@Grapes(
        @Grab(group='com.squareup.okhttp3', module='okhttp', version='3.9.1')
)

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import groovy.json.JsonBuilder
import groovy.transform.Field
import okhttp3.*
import java.util.Random

import java.nio.path.*

@Field String host
@Field int port
@Field int size

Random rand = new Random()
parseArgs(args)
for (i=0; i<size; i++) {
    String jsonBody = generateClient(rand)
    sendRequest(host, port, jsonBody)
}



def generateClient(rand){
    randomInt = rand.nextInt(10)
    def builder = new JsonBuilder()
    def root = builder{
        clientSelf  {
            age  {
                type  "nonScalableFixed"
                value  16+randomInt
            }

            height  {
                type  "nonScalableFixed"
                value  165+randomInt
            }

            weight  {
                type  "nonScalableFixed"
                value  85+randomInt
            }
        }

        clientSearching  {
            age  {
                type  "nonScalableRanged"
                lower  17
                upper  23
            }

            height  {
                type  "scalableRanged"
                lower  150
                upper  201
                priority  6
            }

            weight  {
                type  "scalableFixed"
                value  90
                priority  4
            }
        }
    }
    return builder.toString();
}

def parseArgs(String[] args)
{
    JCommander jCommander = new JCommander()
    def parameters = new EnrollParameters()
    jCommander.addObject(parameters)
    jCommander.parse(args)

    host = parameters.host
    port = parameters.port
    size = parameters.size
}


def sendRequest(String host, int port, String body)
{
    OkHttpClient client = new OkHttpClient()
    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)
    Request request = new Request.Builder()
            .url("http://$host:$port")
            .post(requestBody)
            .build()
    Response response = client.newCall(request).execute()
    println response.body().string()
}



@Parameters(separators = '=')
class EnrollParameters
{
    @Parameter(names = ['-h', '--host'])
    private final String host = 'localhost'

    @Parameter(names = ['-p', '--port'])
    private final int port = 8080

    @Parameter(names = ['-s', '--size'], required = true)
    private final int size
}

