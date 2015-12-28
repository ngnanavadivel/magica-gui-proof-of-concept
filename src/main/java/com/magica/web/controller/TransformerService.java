package com.magica.web.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codemagic.magica.Automator;
import com.codemagic.magica.AutomatorRequest;

@Controller
@RequestMapping("/")
public class TransformerService {

   private String workspaceDir = "C:/Users/gnanavad/Documents/stash-all-in-one/magica-gui/workspace";

   @RequestMapping(value = "services/transform",
                   method = RequestMethod.POST,
                   produces = { MediaType.APPLICATION_JSON_VALUE })
   public @ResponseBody String transform(@RequestParam("service") String service,
                                         @RequestParam("src_schema") String srcSchema,
                                         @RequestParam("tgt_schema") String tgtSchema,
                                         @RequestParam("mapping") String mapping,
                                         @RequestParam("src_data") String srcData,
                                         @RequestParam("src_fmt") String srcFmt,
                                         @RequestParam("tgt_fmt") String tgtFmt) throws Exception {

      initServiceWorkspace(service);

      String sourceSchemaFile = resourcesDirectory(service) + "/source.xsd";
      String tgtSchemaFile = resourcesDirectory(service) + "/target.xsd";
      String mappingFile = resourcesDirectory(service) + "/mapping.properties";

      writeAsFile(sourceSchemaFile, srcSchema);
      writeAsFile(tgtSchemaFile, tgtSchema);
      writeAsFile(mappingFile, mapping);

      AutomatorRequest req = new AutomatorRequest();
      req.srcFormat = AutomatorRequest.FORMAT.valueOf(srcFmt);
      req.destFormat = AutomatorRequest.FORMAT.valueOf(tgtFmt);
      req.service = service;
      req.srcSchemaName = "source.xsd";
      req.tgtSchemaName = "target.xsd";
      req.srcRoot = "DEAL";
      req.tgtRoot = "Loan";
      req.baseDir = serviceDirectory(service);
      req.sourceInstance = srcData;
      return new Automator().automate(req);

   }

   private String compiledDirectory(String service) {
      return serviceDirectory(service) + "/compiled";
   }

   private void createDirectory(String dir) {
      if (dir != null) {
         File file = new File(dir);
         if (!file.exists()) {
            file.mkdirs();
         } else { // watch out this logic.
            file.delete();
            file.mkdirs();
         }
      }
   }

   private String generatedDirectory(String service) {
      return serviceDirectory(service) + "/generated";
   }

   private void initServiceWorkspace(String service) {
      createDirectory(serviceDirectory(service));
      createDirectory(resourcesDirectory(service));
      createDirectory(generatedDirectory(service));
      createDirectory(compiledDirectory(service));
      createDirectory(jarDirectory(service));
   }

   private String jarDirectory(String service) {
      return serviceDirectory(service) + "/jar";
   }

   private String resourcesDirectory(String service) {
      return serviceDirectory(service) + "/resources";
   }

   private String serviceDirectory(String service) {
      return workspaceDir + "/" + service;
   }

   private void writeAsFile(String file, String contents) throws IOException {
      FileUtils.writeStringToFile(new File(file), contents);
   }
}
