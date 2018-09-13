/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Russian;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

/**
 *
 * @author raushan
 */
public class AutoCorrector {
    JLanguageTool langTool;
    public AutoCorrector() throws Exception{
        langTool = new JLanguageTool(new Russian());
        for (Rule rule : langTool.getAllRules()) {
          if (!rule.isDictionaryBasedSpellingRule()) {
            langTool.disableRule(rule.getId());
          }
        }
        
    }
    public List<String> SuggestedCorrections(String word) throws IOException{
        List<String> suggestedCorrections = new ArrayList<String>();
        List<RuleMatch> matches = langTool.check(word);
        for (RuleMatch match : matches) {
          System.out.println("Potential typo at characters " +match.getFromPos() + "-" + match.getToPos() + ": " +match.getMessage());
          System.out.println("Suggested correction(s): " +suggestedCorrections.addAll(match.getSuggestedReplacements()));
        }
        return suggestedCorrections;
    }
    
    public String correct(String word) throws Exception{
        String correctedWord = null;
        if (this.SuggestedCorrections(word).size()>0){
            correctedWord = this.SuggestedCorrections(word).get(0);
        }
        return correctedWord;
    }
    
    public static void main(String[] args) throws Exception{
        AutoCorrector autocorrector = new AutoCorrector();
        System.out.println(autocorrector.correct("конакт"));
    }
}
