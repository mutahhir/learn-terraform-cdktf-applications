package com.mycompany.app;

import java.io.File;
import java.util.Map;

import com.mycompany.constructs.*;
import software.constructs.Construct;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.providers.kubernetes.provider.*;

public class MainStack extends TerraformStack {
        public MainStack(final Construct scope, final String id) {
                super(scope, id);

                new KubernetesProvider(this, "provider", KubernetesProviderConfig.builder()
                                .configPath(new File("../../kubeconfig.yaml").getAbsolutePath())
                                .build());

                SimpleKubernetesWebApp app_backend = new SimpleKubernetesWebApp(this, "app_backend",
                                new SimpleKubernetesWebAppConfig()
                                                .setImage("localhost:5000/nocorp-backend:latest")
                                                .setReplicas(1)
                                                .setApp("myapp")
                                                .setComponent("backend")
                                                .setEnvironment("dev")
                                                .setPort(30002));

                new SimpleKubernetesWebApp(this, "app_frontend", new SimpleKubernetesWebAppConfig()
                                .setImage("localhost:5000/nocorp-frontend:latest")
                                .setReplicas(3)
                                .setApp("myapp")
                                .setComponent("frontend")
                                .setEnvironment("dev")
                                .setPort(30001)
                                .setEnv(Map.of("BACKEND_APP_URL",
                                                String.format("http://localhost:%d", app_backend.config.port()))));

        }
}
