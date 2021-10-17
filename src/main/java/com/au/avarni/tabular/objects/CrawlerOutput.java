package com.au.avarni.tabular.objects;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class CrawlerOutput implements Jsonable {

  private CrawlerStatusOutput status;

  @Override
  public String toJson() {
    final StringWriter writable = new StringWriter();
    try {
      this.toJson(writable);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return writable.toString();
  }

  @Override
  public void toJson(Writer writer) throws IOException {
    final JsonObject json = new JsonObject();
    json.put("status", this.status);
    json.toJson(writer);
  }
}
